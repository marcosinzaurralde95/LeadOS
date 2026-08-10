import { describe, expect, it } from 'vitest';
import { toAst } from './blueprint.js';
import { plan } from './planner.js';

const blueprint = {
  apiVersion: 'factory.leados.dev/v1' as const,
  project: { name: 'Demo', type: 'saas' as const },
  stack: { frontend: 'nextjs', backend: 'nextjs', database: 'supabase', ai: 'openrouter', deploy: 'vercel' },
  modules: ['foundation', 'sales', 'support'],
  outputs: { github: true, notion: true }
};

describe('planner', () => {
  it('creates deterministic module dependencies', () => {
    const result = plan(toAst(blueprint));
    expect(result.tasks.map((task) => task.dependsOn)).toEqual([[], ['module:foundation'], ['module:sales']]);
  });
});
