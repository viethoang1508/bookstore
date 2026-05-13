/** @type {import("next").NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  async rewrites() {
    const gateway = process.env.API_GATEWAY_URL ?? process.env.NEXT_PUBLIC_API_GATEWAY_URL ?? "http://localhost:8282";
    return [
      {
        source: "/api-gateway/:path*",
        destination: `${gateway.replace(/\/$/, "")}/:path*`,
      },
    ];
  },
};

export default nextConfig;
