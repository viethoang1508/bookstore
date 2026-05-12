"use client";

import { useQuery } from "@tanstack/react-query";

import type { BookSummary } from "@/types/book";

export function BooksPlaceholder() {
  const query = useQuery<BookSummary[]>({
    queryKey: ["books", "placeholder"],
    queryFn: async () => [],
  });

  if (query.isPending) {
    return <p className="text-sm text-muted-foreground">Loading catalog…</p>;
  }

  if (query.isError) {
    return <p className="text-sm text-destructive">Could not load books.</p>;
  }

  if (!query.data?.length) {
    return <p className="text-sm text-muted-foreground">No books yet — wire book_service via the API gateway.</p>;
  }

  return (
    <ul className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {query.data.map((b) => (
        <li key={b.id} className="rounded-lg border p-4">
          <p className="font-medium">{b.title}</p>
        </li>
      ))}
    </ul>
  );
}
