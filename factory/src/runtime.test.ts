import { describe, expect, it } from 'vitest';
import { loadBlueprint, toAst } from './blueprint.js';
import { plan } from './planner.js';
import { ModuleWorker } from './workers.js';
import { Runtime } from './runtime.js';

const blueprint = {
  apiVersion: 'factory.leados.dev/v1' as const,
  project: { name: 'Runtime Test', type: 'saas' as const },
  stack: {
    frontend: 'nextjs', backend: 'nextjs', database: 'supabase',
    orm: 'drizzle', auth: 'supabase', ai: 'openrouter', deploy: 'vercel'
  },
  modules: [
    'foundation',
    { id: 'sales', dependsOn: ['foundation'] },
    { id: 'support', dependsOn: ['foundation'] }
  ],
  outputs: { github: false, notion: false }
};

describe('Runtime', () => {
  it('ejecuta tareas respetando dependencias', async () => {
    const project = toAst(blueprint);
    const artifacts = await new Runtime({ workers: [new ModuleWorker()] }).execute(project, plan(project));

    expect(artifacts).toHaveLength(3);
    expect(artifacts.map((artifact) => artifact.id)).toEqual([
      'artifact:foundation:manifest',
      'artifact:sales:manifest',
      'artifact:support:manifest'
    ]);
  });
});

void loadBlueprint;
