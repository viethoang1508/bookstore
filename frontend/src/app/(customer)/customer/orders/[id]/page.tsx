type OrderDetailPageProps = {
  params: { id: string };
};

export default function CustomerOrderDetailPage({ params }: OrderDetailPageProps) {
  return (
    <section className="rounded-lg border p-6">
      <h2 className="text-lg font-medium">Order detail</h2>
      <p className="mt-2 text-sm text-muted-foreground">Order id: {params.id}</p>
    </section>
  );
}
