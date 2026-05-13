"use client";

import { useEffect, useState } from "react";

import { adminBooksApi, type AdminBook } from "@/features/admin-books";

export default function AdminBooksListPage() {
  const [books, setBooks] = useState<AdminBook[]>([]);

  const load = async () => setBooks(await adminBooksApi.list());

  useEffect(() => { void load(); }, []);

  return (
    <section className="space-y-4 rounded-lg border p-6">
        <h2 className="text-lg font-medium">Books</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead><tr className="border-b text-left"><th className="px-3 py-2">Title</th><th className="px-3 py-2">Author</th><th className="px-3 py-2">Stock</th><th className="px-3 py-2">Price</th><th className="px-3 py-2">Actions</th></tr></thead>
            <tbody>
              {books.map((book) => (
                <tr key={book.id} className="border-b">
                  <td className="px-3 py-2">{book.title}</td><td className="px-3 py-2">{book.authorName ?? "-"}</td><td className="px-3 py-2">{book.stock ?? 0}</td><td className="px-3 py-2">{book.price ?? 0}</td>
                  <td className="px-3 py-2"><button type="button" className="text-red-600 underline" onClick={async () => { await adminBooksApi.delete(book.id); await load(); }}>Xóa</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
    </section>
  );
}
