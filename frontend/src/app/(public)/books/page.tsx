import { BooksPlaceholder } from "@/features/books/components/books-placeholder";

export default function BooksListingPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Books</h1>
        <p className="text-sm text-muted-foreground">Listing powered by TanStack Query (placeholder data).</p>
      </div>
      <BooksPlaceholder />
    </div>
  );
}
