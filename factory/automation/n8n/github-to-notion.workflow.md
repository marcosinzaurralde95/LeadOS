# n8n workflow contract — GitHub → Notion

This document defines the automation contract before we commit an n8n export JSON. The export is deliberately deferred until the Notion workspace/database IDs and authentication method are confirmed, preventing a workflow that imports successfully but cannot run.

## Trigger

GitHub `push` event on the documentation paths of the repository.

## Flow

```text
GitHub Webhook
  -> Filter documentation changes
  -> Fetch changed Markdown
  -> Convert Markdown to Notion blocks
  -> Upsert Notion page by stable document key
  -> Record sync result
```

## Required environment variables

- `GITHUB_TOKEN`
- `NOTION_TOKEN`
- `NOTION_PARENT_PAGE_ID`
- `FACTORY_REPOSITORY`

No credentials belong in the repository.

## Idempotency

Each document uses a stable key derived from repository + path. The workflow must update the existing Notion page when that key exists and create it otherwise.

## Failure policy

- GitHub fetch failure: retry, then fail the execution.
- Markdown conversion failure: fail without mutating Notion.
- Notion rate-limit/5xx: retry with backoff.
- Partial sync: persist failed document keys for replay.

## Why this is not yet an importable JSON

n8n workflows contain instance-specific credential references and Notion destination IDs. Creating a generic export would encourage hard-coded secrets or an invalid destination. The next implementation step is to parameterize these values and produce an importable workflow after the target Notion database/page is identified.
