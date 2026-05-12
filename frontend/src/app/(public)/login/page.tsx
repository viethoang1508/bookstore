import { LoginForm } from "@/features/auth";

export default function LoginPage() {
  return (
    <div className="mx-auto flex max-w-lg flex-col items-center gap-6 py-4">
      <div className="text-center">
        <h1 className="text-2xl font-semibold tracking-tight">Login</h1>
        <p className="text-sm text-muted-foreground">Public route — middleware protects /customer and /admin only.</p>
      </div>
      <LoginForm />
    </div>
  );
}
