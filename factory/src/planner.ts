import { ExecutionPlan, ProjectNode } from './types.js';

export function plan(project: ProjectNode): ExecutionPlan {
  const tasks = project.modules.map((module) => ({
    id: `module:${module.id}`,
    kind: 'module' as const,
    module: module.id,
    dependsOn: module.dependsOn.map((dependency) => `module:${dependency}`)
  }));

  return { project: project.name, tasks };
}

export function formatPlan(planResult: ExecutionPlan): string {
  const lines = [`Project: ${planResult.project}`, 'Execution plan:'];
  for (const task of planResult.tasks) {
    const deps = task.dependsOn.length ? ` <- ${task.dependsOn.join(', ')}` : '';
    lines.push(`- ${task.id}${deps}`);
  }
  return lines.join('\n');
}
