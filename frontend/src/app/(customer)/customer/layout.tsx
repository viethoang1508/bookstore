export default function CustomerSectionLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="space-y-8">
      <div>
        <p className="text-sm text-muted-foreground">Customer portal</p>
        <h1 className="text-2xl font-semibold tracking-tight">Account area</h1>
        <p className="text-sm text-muted-foreground">Requires a valid access token cookie (see middleware).</p>
      </div>
      {children}
    </div>
  );
}
