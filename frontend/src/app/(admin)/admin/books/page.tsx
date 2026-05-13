"use client";

import { useEffect, useState } from "react";

import { adminBooksApi, type AdminBook, type BookPayload } from "@/features/admin-books";

import { ApiError } from "@/lib/api-client/errors";

const initialForm: BookPayload = { title: "", isbn: "", slug: "", authorName: "", publisherName: "", publishYear: new Date().getFullYear(), price: 1, stock: 1, description: "", thumbnailUrl: "" };
export default function AdminBooksListPage() {
  const [books, setBooks] = useState<AdminBook[]>([]);
  const [form, setForm] = useState<BookPayload>(initialForm);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setError(null);
      setBooks(await adminBooksApi.list());
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Không thể tải danh sách sách.");
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const submit = async () => {
    if (!form.title.trim()) return;
    try {
      setError(null);
      if (editingId) {
        await adminBooksApi.update(editingId, form);
      } else {
        await adminBooksApi.create(form);
      }
      setEditingId(null);
      setForm(initialForm);
      await load();
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Không thể lưu sách.");
    }
  };

  return (
    <section className="space-y-4 rounded-lg border p-6">
        <h2 className="text-lg font-medium">Books</h2>
      <div className="grid gap-2 rounded border p-3 md:grid-cols-4">
        <input className="rounded border px-2 py-1 text-sm" placeholder="ISBN" value={form.isbn ?? ""} onChange={(e) => setForm((s) => ({ ...s, isbn: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="Slug" value={form.slug ?? ""} onChange={(e) => setForm((s) => ({ ...s, slug: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="Title" value={form.title} onChange={(e) => setForm((s) => ({ ...s, title: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="Author" value={form.authorName ?? ""} onChange={(e) => setForm((s) => ({ ...s, authorName: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="Publisher" value={form.publisherName ?? ""} onChange={(e) => setForm((s) => ({ ...s, publisherName: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Publish year" value={form.publishYear ?? 0} onChange={(e) => setForm((s) => ({ ...s, publishYear: Number(e.target.value) }))} />
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Price" value={form.price} onChange={(e) => setForm((s) => ({ ...s, price: Number(e.target.value) }))} />
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Stock" value={form.stock} onChange={(e) => setForm((s) => ({ ...s, stock: Number(e.target.value) }))} />
        <input className="rounded border px-2 py-1 text-sm md:col-span-2" placeholder="Thumbnail URL" value={form.thumbnailUrl ?? ""} onChange={(e) => setForm((s) => ({ ...s, thumbnailUrl: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm md:col-span-2" placeholder="Description" value={form.description ?? ""} onChange={(e) => setForm((s) => ({ ...s, description: e.target.value }))} />
        <button type="button" className="rounded bg-black px-3 py-2 text-sm text-white" onClick={() => void submit()}>{editingId ? "Lưu cập nhật" : "Thêm sách"}</button>
        {editingId ? <button type="button" className="rounded border px-3 py-2 text-sm" onClick={() => { setEditingId(null); setForm(initialForm); }}>Hủy</button> : null}
      </div>
      {error ? <p className="text-sm text-red-600">{error}</p> : null}
      <div className="overflow-x-auto">
        <table className="min-w-full text-sm">
          <thead><tr className="border-b text-left"><th className="px-3 py-2">Title</th><th className="px-3 py-2">Author</th><th className="px-3 py-2">Stock</th><th className="px-3 py-2">Price</th><th className="px-3 py-2">Actions</th></tr></thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id} className="border-b">
                <td className="px-3 py-2">{book.title}</td><td className="px-3 py-2">{book.authorName ?? "-"}</td><td className="px-3 py-2">{book.stock ?? 0}</td><td className="px-3 py-2">{book.price ?? 0}</td>
                <td className="space-x-3 px-3 py-2">
                  <button type="button" className="text-blue-600 underline" onClick={() => { setEditingId(book.id); setForm({ title: book.title, isbn: book.isbn ?? "", slug: book.slug ?? "", authorName: book.authorName ?? "", publisherName: book.publisherName ?? "", publishYear: book.publishYear ?? new Date().getFullYear(), price: book.price ?? 1, stock: book.stock ?? 1, description: book.description ?? "", thumbnailUrl: book.thumbnailUrl ?? "" }); }}>Sửa</button>
                  <button type="button" className="text-red-600 underline" onClick={async () => { try { setError(null); await adminBooksApi.delete(book.id); await load(); } catch (e) { setError(e instanceof ApiError ? e.message : "Không thể xóa sách."); } }}>Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
