import { readFile } from 'node:fs/promises';
import { parse } from 'yaml';
import Ajv from 'ajv';
import { ModuleDefinition, ProjectBlueprint, ProjectNode } from './types.js';
import schema from '../schemas/factory.schema.json' with { type: 'json' };

const ajv = new Ajv({ allErrors: true, strict: false });
const validateSchema = ajv.compile<ProjectBlueprint>(schema);

function normalizeModule(module: string | ModuleDefinition) {
  return typeof module === 'string' ? { id: module, dependsOn: [] } : { id: module.id, dependsOn: module.dependsOn ?? [] };
}

function validateSemantics(blueprint: ProjectBlueprint): void {
  const modules = blueprint.modules.map(normalizeModule);
  const ids = new Set(modules.map((module) => module.id));

  if (ids.size !== modules.length) {
    throw new Error('Invalid blueprint: modules must have unique ids');
  }

  for (const module of modules) {
    for (const dependency of module.dependsOn) {
      if (!ids.has(dependency)) {
        throw new Error(`Invalid blueprint: module '${module.id}' depends on unknown module '${dependency}'`);
      }
      if (dependency === module.id) {
        throw new Error(`Invalid blueprint: module '${module.id}' cannot depend on itself`);
      }
    }
  }

  const visiting = new Set<string>();
  const visited = new Set<string>();
  const graph = new Map(modules.map((module) => [module.id, module.dependsOn]));

  const visit = (id: string) => {
    if (visiting.has(id)) throw new Error(`Invalid blueprint: cyclic module dependency involving '${id}'`);
    if (visited.has(id)) return;
    visiting.add(id);
    for (const dependency of graph.get(id) ?? []) visit(dependency);
    visiting.delete(id);
    visited.add(id);
  };

  for (const id of ids) visit(id);
}

export async function loadBlueprint(path: string): Promise<ProjectBlueprint> {
  const source = await readFile(path, 'utf8');
  const value = parse(source) as unknown;
  if (!validateSchema(value)) {
    const details = (validateSchema.errors ?? []).map((e) => `${e.instancePath || '/'} ${e.message}`).join('; ');
    throw new Error(`Invalid blueprint: ${details}`);
  }
  validateSemantics(value);
  return value;
}

export function toAst(blueprint: ProjectBlueprint): ProjectNode {
  const modules = blueprint.modules.map((module) => {
    const normalized = normalizeModule(module);
    return { kind: 'Module' as const, id: normalized.id, dependsOn: normalized.dependsOn };
  });

  return {
    kind: 'Project',
    name: blueprint.project.name,
    type: blueprint.project.type,
    stack: blueprint.stack,
    modules,
    outputs: blueprint.outputs
  };
}
