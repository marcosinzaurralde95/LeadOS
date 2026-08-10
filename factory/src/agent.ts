export interface AgentSpec {
  apiVersion: 'factory.leados.dev/agent/v1';
  id: string;
  role: string;
  goal: string;
  capabilities: string[];
  permissions: string[];
  inputs: string[];
  outputs: string[];
  constraints: string[];
  knowledgePacks?: string[];
  kpis?: string[];
}

export interface AgentTask {
  objective: string;
  context?: Record<string, unknown>;
}

export interface ExecutionContextInput {
  architecture: string[];
  standards: string[];
  knowledge: string[];
  task: AgentTask;
}

export function compileExecutionContext(agent: AgentSpec, input: ExecutionContextInput): string {
  const sections = [
    `# Rol\n${agent.role}`,
    `# Objetivo\n${agent.goal}`,
    `# Capacidades\n${agent.capabilities.map((item) => `- ${item}`).join('\n')}`,
    `# Permisos\n${agent.permissions.map((item) => `- ${item}`).join('\n')}`,
    `# Entradas\n${agent.inputs.map((item) => `- ${item}`).join('\n')}`,
    `# Salidas\n${agent.outputs.map((item) => `- ${item}`).join('\n')}`,
    `# Restricciones\n${agent.constraints.map((item) => `- ${item}`).join('\n')}`,
    `# Arquitectura\n${input.architecture.map((item) => `- ${item}`).join('\n')}`,
    `# Estándares\n${input.standards.map((item) => `- ${item}`).join('\n')}`,
    `# Conocimiento\n${input.knowledge.map((item) => `- ${item}`).join('\n')}`,
    `# Tarea\n${input.task.objective}`
  ];

  if (input.task.context && Object.keys(input.task.context).length > 0) {
    sections.push(`# Contexto adicional\n${JSON.stringify(input.task.context, null, 2)}`);
  }

  return sections.join('\n\n');
}
