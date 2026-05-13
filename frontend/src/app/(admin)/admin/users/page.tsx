"use client";

import { useEffect, useState } from "react";

import { adminUsersApi, type CreateAdminPayload } from "@/features/admin-users";

type Me = { id: string; firstName?: string; lastName?: string; email?: string };

const initialForm: CreateAdminPayload = { email: "", username: "", password: "", fullName: "", phone: "" };

export default function AdminUsersPage() {
  const [me, setMe] = useState<Me | null>(null);
  const [form, setForm] = useState<CreateAdminPayload>(initialForm);

  useEffect(() => {
    adminUsersApi.me().then(setMe);
  }, []);

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Users</h2>
      {me && <div className="rounded border p-3 text-sm">Current: {me.firstName} {me.lastName} - {me.email}</div>}
      <div className="grid gap-2 rounded border p-3 md:grid-cols-3">
        <input className="rounded border px-2 py-1" placeholder="Email" value={form.email} onChange={(e) => setForm((s) => ({ ...s, email: e.target.value }))} />
        <input className="rounded border px-2 py-1" placeholder="Username" value={form.username} onChange={(e) => setForm((s) => ({ ...s, username: e.target.value }))} />
        <input className="rounded border px-2 py-1" placeholder="Password" value={form.password} onChange={(e) => setForm((s) => ({ ...s, password: e.target.value }))} />
        <input className="rounded border px-2 py-1" placeholder="Full name" value={form.fullName} onChange={(e) => setForm((s) => ({ ...s, fullName: e.target.value }))} />
        <input className="rounded border px-2 py-1" placeholder="Phone (+84...)" value={form.phone} onChange={(e) => setForm((s) => ({ ...s, phone: e.target.value }))} />
        <button type="button" className="rounded bg-black px-3 py-2 text-sm text-white" onClick={async () => { await adminUsersApi.createAdmin(form); setForm(initialForm); }}>Create admin</button>
      </div>
    </section>
  );
}
