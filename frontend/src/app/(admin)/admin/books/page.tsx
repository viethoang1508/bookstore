"use client";

import { useEffect, useState } from "react";

import { adminBooksApi, type AdminBook, type BookPayload } from "@/features/admin-books";

const initialForm: BookPayload = { title: "", authorName: "", price: 0, stock: 0 };

export default function AdminBooksListPage() {
  const [books, setBooks] = useState<AdminBook[]>([]);
  const [form, setForm] = useState<BookPayload>(initialForm);
  const [editingId, setEditingId] = useState<string | null>(null);

  const load = async () => setBooks(await adminBooksApi.list());

  useEffect(() => {
    void load();
  }, []);

  const submit = async () => {
    if (!form.title.trim()) return;
    if (editingId) {
      await adminBooksApi.update(editingId, form);
    } else {
      await adminBooksApi.create(form);
    }
    setEditingId(null);
    setForm(initialForm);
    await load();
  };

  return (
    <section className="space-y-4 rounded-lg border p-6">
        <h2 className="text-lg font-medium">Books</h2>
      <div className="grid gap-2 rounded border p-3 md:grid-cols-4">
        <input className="rounded border px-2 py-1 text-sm" placeholder="Title" value={form.title} onChange={(e) => setForm((s) => ({ ...s, title: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="Author" value={form.authorName ?? ""} onChange={(e) => setForm((s) => ({ ...s, authorName: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Price" value={form.price} onChange={(e) => setForm((s) => ({ ...s, price: Number(e.target.value) }))} />
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Stock" value={form.stock} onChange={(e) => setForm((s) => ({ ...s, stock: Number(e.target.value) }))} />
        <button type="button" className="rounded bg-black px-3 py-2 text-sm text-white" onClick={() => void submit()}>{editingId ? "Lưu cập nhật" : "Thêm sách"}</button>
        {editingId ? <button type="button" className="rounded border px-3 py-2 text-sm" onClick={() => { setEditingId(null); setForm(initialForm); }}>Hủy</button> : null}
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full text-sm">
          <thead><tr className="border-b text-left"><th className="px-3 py-2">Title</th><th className="px-3 py-2">Author</th><th className="px-3 py-2">Stock</th><th className="px-3 py-2">Price</th><th className="px-3 py-2">Actions</th></tr></thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id} className="border-b">
                <td className="px-3 py-2">{book.title}</td><td className="px-3 py-2">{book.authorName ?? "-"}</td><td className="px-3 py-2">{book.stock ?? 0}</td><td className="px-3 py-2">{book.price ?? 0}</td>
                <td className="space-x-3 px-3 py-2">
                  <button type="button" className="text-blue-600 underline" onClick={() => { setEditingId(book.id); setForm({ title: book.title, authorName: book.authorName ?? "", price: book.price ?? 0, stock: book.stock ?? 0 }); }}>Sửa</button>
                  <button type="button" className="text-red-600 underline" onClick={async () => { await adminBooksApi.delete(book.id); await load(); }}>Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
