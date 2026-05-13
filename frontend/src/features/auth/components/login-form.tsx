"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useRouter, useSearchParams } from "next/navigation";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { authApi } from "@/features/auth/api";
import { type LoginFormValues, loginSchema } from "@/features/auth/schemas/login-schema";

import { toast } from "@/hooks/use-toast";
import { decodeJwtPayload, extractRoleFromPayload } from "@/lib/auth/jwt";
import { useAuthContext } from "@/providers/auth-provider";


export function LoginForm() {
  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: "", password: "" },
  });

  const router = useRouter();
  const params = useSearchParams();
  const { setAuthenticatedSession } = useAuthContext();

  const loginMutation = useMutation({
    mutationFn: (values: LoginFormValues) =>
      authApi.login({
        usernameOrEmail: values.email,
        password: values.password,
      }),
    onSuccess: (token) => {
      const payload = decodeJwtPayload(token.access_token);
      const role = payload ? extractRoleFromPayload(payload) : null;
      if (!role) {
        toast({ title: "Login failed", description: "Could not determine user role from token.", variant: "destructive" });
        return;
      }
      setAuthenticatedSession({ accessToken: token.access_token, refreshToken: token.refresh_token, role });
      const next = params.get("next");
      router.push(next || (role === "ADMIN" ? "/admin" : "/customer/profile"));
      toast({ title: "Logged in", description: "Welcome back." });
    },
  });

  function onSubmit(values: LoginFormValues) {
    loginMutation.mutate(values);
  }

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>Sign in</CardTitle>
        <CardDescription>Use your bookstore account.</CardDescription>
      </CardHeader>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <CardContent className="space-y-4">
            <FormField control={form.control} name="email" render={({ field }) => (<FormItem><FormLabel>Email</FormLabel><FormControl><Input autoComplete="email" type="email" placeholder="you@example.com" {...field} /></FormControl><FormMessage /></FormItem>)} />
            <FormField control={form.control} name="password" render={({ field }) => (<FormItem><FormLabel>Password</FormLabel><FormControl><Input autoComplete="current-password" type="password" {...field} /></FormControl><FormMessage /></FormItem>)} />
          </CardContent>
          <CardFooter>
            <Button className="w-full" type="submit" disabled={loginMutation.isPending}>{loginMutation.isPending ? "Signing in..." : "Continue"}</Button>
          </CardFooter>
        </form>
      </Form>
    </Card>
  );
}
