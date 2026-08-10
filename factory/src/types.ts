export interface ProjectBlueprint {
  apiVersion: 'factory.leados.dev/v1';
  project: { name: string; type: 'saas' | 'api' | 'library' | 'agent' | 'automation' };
  stack: {
    frontend: string;
    backend: string;
    database: string;
    orm?: string;
    auth?: string;
    ai: string;
    deploy: string;
  };
  modules: string[];
  outputs: { github: boolean; notion: boolean };
}

export interface ProjectNode {
  kind: 'Project';
  name: string;
  type: ProjectBlueprint['project']['type'];
  stack: ProjectBlueprint['stack'];
  modules: ModuleNode[];
  outputs: ProjectBlueprint['outputs'];
}

export interface ModuleNode {
  kind: 'Module';
  id: string;
  dependsOn: string[];
}

export interface ExecutionTask {
  id: string;
  kind: 'module';
  module: string;
  dependsOn: string[];
}

export interface ExecutionPlan {
  project: string;
  tasks: ExecutionTask[];
}
