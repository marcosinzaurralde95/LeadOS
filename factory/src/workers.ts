import { ExecutionTask } from './types.js';
import { Artifact, ExecutionContext, Worker } from './runtime.js';

export class ModuleWorker implements Worker {
  readonly id = 'module-worker';
  readonly capabilities = ['module'] as const;

  async execute(task: ExecutionTask, context: ExecutionContext): Promise<Artifact[]> {
    if (task.kind !== 'module') {
      throw new Error(`El worker de módulos no puede ejecutar '${task.kind}'`);
    }

    return [{
      id: `artifact:${task.module}:manifest`,
      kind: 'module-manifest',
      path: `modules/${task.module}/factory-manifest.json`,
      content: JSON.stringify({
        project: context.project.name,
        module: task.module,
        dependsOn: task.dependsOn.map((id) => id.replace(/^module:/, ''))
      }, null, 2) + '\n',
      metadata: { generatedBy: this.id }
    }];
  }
}
