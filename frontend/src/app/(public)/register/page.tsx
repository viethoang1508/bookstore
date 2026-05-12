import { RegisterForm } from "@/features/auth";

export default function RegisterPage() {
  return (
    <div className="mx-auto flex max-w-lg flex-col items-center gap-6 py-4">
      <div className="text-center">
        <h1 className="text-2xl font-semibold tracking-tight">Register</h1>
        <p className="text-sm text-muted-foreground">Create an account once auth_service is connected.</p>
      </div>
      <RegisterForm />
    </div>
  );
}
