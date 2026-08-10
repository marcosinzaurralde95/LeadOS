import { ExecutionPlan, ExecutionTask, ProjectNode } from './types.js';

export interface Artifact {
  id: string;
  kind: string;
  path?: string;
  content?: string;
  metadata?: Record<string, unknown>;
}

export interface ExecutionContext {
  project: ProjectNode;
  plan: ExecutionPlan;
  artifacts: Artifact[];
}

export interface Worker {
  readonly id: string;
  readonly capabilities: readonly string[];
  execute(task: ExecutionTask, context: ExecutionContext): Promise<Artifact[]>;
}

export interface RuntimeOptions {
  workers: readonly Worker[];
}

export class RuntimeError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'RuntimeError';
  }
}

export class Runtime {
  private readonly workersByKind: Map<string, Worker>;

  constructor(private readonly options: RuntimeOptions) {
    this.workersByKind = new Map();
    for (const worker of options.workers) {
      for (const capability of worker.capabilities) {
        if (this.workersByKind.has(capability)) {
          throw new RuntimeError(`Ya existe un worker para la capacidad '${capability}'`);
        }
        this.workersByKind.set(capability, worker);
      }
    }
  }

  async execute(project: ProjectNode, plan: ExecutionPlan): Promise<Artifact[]> {
    const context: ExecutionContext = { project, plan, artifacts: [] };
    const completed = new Set<string>();
    const pending = new Map(plan.tasks.map((task) => [task.id, task]));

    while (pending.size > 0) {
      const ready = [...pending.values()].filter((task) => task.dependsOn.every((dependency) => completed.has(dependency)));

      if (ready.length === 0) {
        const unresolved = [...pending.keys()].join(', ');
        throw new RuntimeError(`No se puede avanzar el plan; tareas sin resolver: ${unresolved}`);
      }

      const batch = await Promise.all(ready.map(async (task) => {
        const worker = this.workersByKind.get(task.kind);
        if (!worker) {
          throw new RuntimeError(`No existe un worker para la capacidad '${task.kind}'`);
        }
        return { task, artifacts: await worker.execute(task, context) };
      }));

      for (const result of batch) {
        context.artifacts.push(...result.artifacts);
        completed.add(result.task.id);
        pending.delete(result.task.id);
      }
    }

    return context.artifacts;
  }
}
