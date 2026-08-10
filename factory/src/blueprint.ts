import { readFile } from 'node:fs/promises';
import { parse } from 'yaml';
import Ajv from 'ajv';
import { ProjectBlueprint, ProjectNode } from './types.js';
import schema from '../schemas/factory.schema.json' with { type: 'json' };

const ajv = new Ajv({ allErrors: true, strict: false });
const validateSchema = ajv.compile<ProjectBlueprint>(schema);

export async function loadBlueprint(path: string): Promise<ProjectBlueprint> {
  const source = await readFile(path, 'utf8');
  const value = parse(source) as unknown;
  if (!validateSchema(value)) {
    const details = (validateSchema.errors ?? []).map((e) => `${e.instancePath || '/'} ${e.message}`).join('; ');
    throw new Error(`Invalid blueprint: ${details}`);
  }
  return value;
}

export function toAst(blueprint: ProjectBlueprint): ProjectNode {
  const modules = blueprint.modules.map((id, index) => ({
    kind: 'Module' as const,
    id,
    dependsOn: index === 0 ? [] : [blueprint.modules[index - 1]!] 
  }));

  return {
    kind: 'Project',
    name: blueprint.project.name,
    type: blueprint.project.type,
    stack: blueprint.stack,
    modules,
    outputs: blueprint.outputs
  };
}
