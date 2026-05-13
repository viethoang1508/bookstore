"use client";

import { useEffect, useState } from "react";

import { adminCategoriesApi, type AdminCategory } from "@/features/admin-categories";
import { ApiError } from "@/lib/api-client/errors";

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState<AdminCategory[]>([]);
  const [name, setName] = useState("");
  const [slug, setSlug] = useState("");
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setError(null);
      setCategories(await adminCategoriesApi.list());
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Không thể tải category.");
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const submit = async () => {
    if (!name.trim()) return;
    const payload = { name, slug: slug || undefined };
    try {
      setError(null);
      if (editingId) {
        await adminCategoriesApi.update(editingId, payload);
      } else {
        await adminCategoriesApi.create(payload);
      }
      setName("");
      setSlug("");
      setEditingId(null);
      await load();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Không thể lưu category.");
    }
  };

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Categories</h2>
      <div className="grid gap-2 rounded border p-3 md:grid-cols-3">
        <input className="rounded border px-2 py-1 text-sm" placeholder="Category name" value={name} onChange={(e) => setName(e.target.value)} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="slug (optional)" value={slug} onChange={(e) => setSlug(e.target.value)} />
        <button type="button" className="rounded bg-black px-3 py-2 text-sm text-white" onClick={() => void submit()}>{editingId ? "Lưu cập nhật" : "Thêm category"}</button>
      </div>
      {error ? <p className="text-sm text-red-600">{error}</p> : null}
      {categories.map((category) => (
        <article key={category.id} className="flex items-center justify-between rounded border p-3 text-sm">
          <div>{category.name} (/{category.slug})</div>
          <div className="space-x-3">
            <button type="button" className="text-blue-600 underline" onClick={() => { setEditingId(category.id); setName(category.name); setSlug(category.slug); }}>Sửa</button>
            <button
              type="button"
              className="text-red-600 underline"
              onClick={async () => {
                try {
                  setError(null);
                  await adminCategoriesApi.delete(category.id);
                  await load();
                } catch (e) {
                  setError(e instanceof ApiError ? e.message : "Không thể xóa category.");
                }
              }}
            >
              Xóa
            </button>
          </div>
        </article>
      ))}
    </section>
  );
}