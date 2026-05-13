"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
import { booksApi } from "@/features/books";
import type { BookSummary } from "@/types";

export default function BooksListingPage() {
  const [books, setBooks] = useState<BookSummary[]>([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => { booksApi.list().then(setBooks).finally(() => setLoading(false)); }, []);
  return <div className="space-y-4"><h1 className="text-2xl font-semibold">Books</h1>{loading?<p>Loading...</p>:<div className="grid gap-3">{books.map((b)=><Link key={b.id} href={`/books/${b.id}`} className="rounded border p-3 text-sm">{b.title} - {String(b.price ?? 0)}</Link>)}</div>}</div>;
}
