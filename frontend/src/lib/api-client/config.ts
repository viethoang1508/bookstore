/**
 * Reads `NEXT_PUBLIC_API_GATEWAY_URL`.
 * Defaults to a same-origin Next.js proxy path to avoid browser CORS/mixed-content issues in local/dev. */
export function getApiGatewayBaseUrl(): string {
  const url = process.env.NEXT_PUBLIC_API_GATEWAY_URL ?? "/api-gateway";
  return url.replace(/\/$/, "");
}
