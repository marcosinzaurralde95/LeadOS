import { describe, expect, it } from 'vitest';
import { AgentSpec, compileExecutionContext } from './agent.js';

describe('Agent context compiler', () => {
  it('combina especificación, arquitectura, estándares y tarea', () => {
    const agent: AgentSpec = {
      apiVersion: 'factory.leados.dev/agent/v1',
      id: 'backend',
      role: 'Ingeniero Backend',
      goal: 'Implementar capacidades backend seguras',
      capabilities: ['module'],
      permissions: ['src', 'tests'],
      inputs: ['task'],
      outputs: ['artifact'],
      constraints: ['No introducir dependencias sin justificación']
    };

    const context = compileExecutionContext(agent, {
      architecture: ['Arquitectura modular'],
      standards: ['TypeScript estricto'],
      knowledge: ['Patrón repository'],
      task: { objective: 'Crear el módulo de ventas' }
    });

    expect(context).toContain('Ingeniero Backend');
    expect(context).toContain('Arquitectura modular');
    expect(context).toContain('TypeScript estricto');
    expect(context).toContain('Crear el módulo de ventas');
  });
});
