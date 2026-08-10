#!/usr/bin/env node
import { Command } from 'commander';
import { loadBlueprint, toAst } from './blueprint.js';
import { plan, formatPlan } from './planner.js';
import { generate } from './generator.js';

const program = new Command();
program.name('factory').description('LeadOS Factory declarative project compiler').version('0.1.0');

program.command('validate').argument('<blueprint>').action(async (path: string) => {
  await loadBlueprint(path);
  console.log(`Valid blueprint: ${path}`);
});

program.command('plan').argument('<blueprint>').action(async (path: string) => {
  const blueprint = await loadBlueprint(path);
  console.log(formatPlan(plan(toAst(blueprint))));
});

program.command('generate')
  .argument('<blueprint>')
  .option('-o, --out <dir>', '.factory-output')
  .action(async (path: string, options: { out: string }) => {
    const blueprint = await loadBlueprint(path);
    const project = toAst(blueprint);
    const files = await generate(project, options.out);
    console.log(`Generated ${files.length} artifacts in ${options.out}`);
  });

program.parseAsync().catch((error: unknown) => {
  console.error(error instanceof Error ? error.message : error);
  process.exitCode = 1;
});
