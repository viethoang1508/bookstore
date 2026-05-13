"use client";

import { useQuery } from "@tanstack/react-query";

import { booksApi } from "@/features/books/api";
export function BooksPlaceholder() {
  const query = useQuery({
    queryKey: ["books", "catalog"],
    queryFn: booksApi.list,
  });

  if (query.isPending) return <p className="text-sm text-muted-foreground">Loading catalog…</p>;
  if (query.isError) return <p className="text-sm text-destructive">Could not load books.</p>;
  if (!query.data?.length) return <p className="text-sm text-muted-foreground">No books found.</p>;

  return (
    <ul className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {query.data.map((b) => (
        <li key={b.id} className="rounded-lg border p-4">
          <p className="font-medium">{b.title}</p>
          {b.author ? <p className="text-sm text-muted-foreground">{b.author}</p> : null}
        </li>
      ))}
    </ul>
  );
}
