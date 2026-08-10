import { readFile } from 'node:fs/promises';
import { parse } from 'yaml';
import Ajv from 'ajv';
import { ModuleDefinition, ProjectBlueprint, ProjectNode } from './types.js';
import schema from '../schemas/factory.schema.json' with { type: 'json' };

type AjvConstructor = new (options?: { allErrors?: boolean; strict?: boolean }) => {
  compile<T>(schema: unknown): ((value: unknown) => value is T) & { errors?: Array<{ instancePath?: string; message?: string }> | null };
};

const AjvCtor = (Ajv as unknown as { default?: AjvConstructor }).default ?? (Ajv as unknown as AjvConstructor);
const ajv = new AjvCtor({ allErrors: true, strict: false });
const validateSchema = ajv.compile<ProjectBlueprint>(schema);

function normalizeModule(module: string | ModuleDefinition) {
  return typeof module === 'string' ? { id: module, dependsOn: [] } : { id: module.id, dependsOn: module.dependsOn ?? [] };
}

function validateSemantics(blueprint: ProjectBlueprint): void {
  const modules = blueprint.modules.map(normalizeModule);
  const ids = new Set(modules.map((module) => module.id));

  if (ids.size !== modules.length) {
    throw new Error('Blueprint inválido: los módulos deben tener identificadores únicos');
  }

  for (const module of modules) {
    for (const dependency of module.dependsOn) {
      if (!ids.has(dependency)) {
        throw new Error(`Blueprint inválido: el módulo '${module.id}' depende del módulo desconocido '${dependency}'`);
      }
      if (dependency === module.id) {
        throw new Error(`Blueprint inválido: el módulo '${module.id}' no puede depender de sí mismo`);
      }
    }
  }

  const visiting = new Set<string>();
  const visited = new Set<string>();
  const graph = new Map(modules.map((module) => [module.id, module.dependsOn]));

  const visit = (id: string): void => {
    if (visiting.has(id)) throw new Error(`Blueprint inválido: dependencia cíclica relacionada con '${id}'`);
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
    const details = (validateSchema.errors ?? [])
      .map((error) => `${error.instancePath || '/'} ${error.message ?? 'error de validación'}`)
      .join('; ');
    throw new Error(`Blueprint inválido: ${details}`);
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
