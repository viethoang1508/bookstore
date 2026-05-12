"use client";

export function readClientCookie(name: string): string | null {
  if (typeof document === "undefined") return null;
  const parts = `; ${document.cookie}`.split(`; ${encodeURIComponent(name)}=`);
  if (parts.length === 2) {
    const value = parts.pop()?.split(";").shift();
    return value ? decodeURIComponent(value) : null;
  }
  return null;
}
