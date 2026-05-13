"use client";

import { useEffect, useState } from "react";

import { adminUsersApi } from "@/features/admin-users";

type Me = { id: string; firstName?: string; lastName?: string; email?: string };

export default function AdminUsersPage() {
  const [me, setMe] = useState<Me | null>(null);

  useEffect(() => { adminUsersApi.me().then(setMe); }, []);

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Users</h2>
      <p className="text-sm text-muted-foreground">Backend hiện chỉ có endpoint user hiện tại, chưa có API danh sách users cho admin.</p>
      {me && <div className="rounded border p-3 text-sm">{me.firstName} {me.lastName} - {me.email}</div>}
    </section>
  );
}
