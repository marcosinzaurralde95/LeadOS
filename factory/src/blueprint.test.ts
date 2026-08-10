import { describe, expect, it } from 'vitest';
import { toAst } from './blueprint.js';
import type { ProjectBlueprint } from './types.js';

const base: ProjectBlueprint = {
  apiVersion: 'factory.leados.dev/v1',
  project: { name: 'Demo', type: 'saas' },
  stack: { frontend: 'nextjs', backend: 'nextjs', database: 'supabase', ai: 'openrouter', deploy: 'vercel' },
  modules: [
    'foundation',
    { id: 'sales', dependsOn: ['foundation'] },
    { id: 'support', dependsOn: ['foundation'] }
  ],
  outputs: { github: true, notion: true }
};

describe('blueprint AST', () => {
  it('preserves explicit dependencies', () => {
    const ast = toAst(base);
    expect(ast.modules).toEqual([
      { kind: 'Module', id: 'foundation', dependsOn: [] },
      { kind: 'Module', id: 'sales', dependsOn: ['foundation'] },
      { kind: 'Module', id: 'support', dependsOn: ['foundation'] }
    ]);
  });
});
