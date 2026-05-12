type AdminBookEditPageProps = {
  params: { id: string };
};

export default function AdminBookEditPage({ params }: AdminBookEditPageProps) {
  return (
    <section className="rounded-lg border p-6">
      <h2 className="text-lg font-medium">Edit book</h2>
      <p className="mt-2 text-sm text-muted-foreground">Book id: {params.id}</p>
    </section>
  );
}
