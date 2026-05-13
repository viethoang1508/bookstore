/**
 * Reads `NEXT_PUBLIC_API_GATEWAY_URL`.
 * Defaults to a same-origin Next.js proxy path to avoid browser CORS/mixed-content issues in local/dev. */
export function getApiGatewayBaseUrl(): string {
  const configuredUrl = process.env.NEXT_PUBLIC_API_GATEWAY_URL?.trim();

  // Browser clients cannot reach "localhost" of the deployment server.
  // When misconfigured, fallback to the same-origin proxy rewrite.
  if (configuredUrl && typeof window !== "undefined") {
    try {
      const parsed = new URL(configuredUrl);
      if (parsed.hostname === "localhost" || parsed.hostname === "127.0.0.1") {
        return "/api-gateway";
      }
    } catch {
      // Ignore invalid absolute URL and keep handling below.
    }
  }

  const url = configuredUrl || "/api-gateway";
  return url.replace(/\/$/, "");
}
