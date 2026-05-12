import Link from "next/link";

import { Button } from "@/components/ui/button";

type BookDetailPageProps = {
  params: { id: string };
};

export default function BookDetailPage({ params }: BookDetailPageProps) {
  return (
    <div className="mx-auto max-w-2xl space-y-4">
      <h1 className="text-2xl font-semibold tracking-tight">Book detail</h1>
      <p className="text-sm text-muted-foreground">Book id: {params.id}</p>
      <p className="text-sm text-muted-foreground">Wire this page to book_service when ready.</p>
      <Button variant="outline" asChild>
        <Link href="/books">Back to listing</Link>
      </Button>
    </div>
  );
}
