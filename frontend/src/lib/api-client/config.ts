/**
 * Reads `NEXT_PUBLIC_API_GATEWAY_URL`. Falls back for local/build only; set the env in production.
 */
export function getApiGatewayBaseUrl(): string {
  const url = process.env.NEXT_PUBLIC_API_GATEWAY_URL ?? "http://localhost:8080";
  return url.replace(/\/$/, "");
}
