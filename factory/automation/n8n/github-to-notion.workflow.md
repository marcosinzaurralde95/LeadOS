# Contrato de workflow n8n — GitHub → Notion

Este documento define el contrato de automatización antes de comprometer una exportación JSON de n8n. La exportación se mantiene deliberadamente pospuesta hasta confirmar los identificadores del espacio/base/página de Notion y el método de autenticación. Así evitamos un workflow que importe correctamente pero no pueda ejecutarse.

## Disparador

Evento `push` de GitHub sobre las rutas de documentación del repositorio.

## Flujo

```text
Webhook de GitHub
  -> Filtrar cambios de documentación
  -> Obtener Markdown modificado
  -> Convertir Markdown a bloques de Notion
  -> Crear o actualizar página de Notion mediante clave documental estable
  -> Registrar resultado de sincronización
```

## Variables de entorno requeridas

- `GITHUB_TOKEN`
- `NOTION_TOKEN`
- `NOTION_PARENT_PAGE_ID`
- `FACTORY_REPOSITORY`

No se almacenan credenciales en el repositorio.

## Idempotencia

Cada documento utiliza una clave estable derivada del repositorio y de su ruta. El workflow debe actualizar la página de Notion existente cuando esa clave ya exista y crearla cuando no exista.

## Política de fallos

- Fallo al obtener datos de GitHub: reintentar y después marcar la ejecución como fallida.
- Fallo de conversión Markdown: fallar sin modificar Notion.
- Rate limit o error 5xx de Notion: reintentar con backoff.
- Sincronización parcial: persistir las claves documentales fallidas para poder reproducirlas.

## Por qué todavía no es un JSON importable

Los workflows de n8n contienen referencias a credenciales específicas de la instancia e identificadores del destino de Notion. Crear una exportación genérica fomentaría secretos codificados o un destino inválido. El siguiente paso de implementación es parametrizar esos valores y generar un workflow importable después de identificar la base o página destino de Notion.
