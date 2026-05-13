"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { authApi } from "@/features/auth/api";
import { type RegisterFormValues, registerSchema } from "@/features/auth/schemas/register-schema";
import { toast } from "@/hooks/use-toast";

export function RegisterForm() {
  const form = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { email: "", password: "", confirmPassword: "" },
  });

  const router = useRouter();

  const registerMutation = useMutation({
    mutationFn: (values: RegisterFormValues) =>
      authApi.register({
        username: values.email,
        email: values.email,
        password: values.password,
      }),
    onSuccess: () => {
      toast({ title: "Registration successful", description: "Please sign in." });
      router.push("/login");
    },
  });

  function onSubmit(values: RegisterFormValues) {
    registerMutation.mutate(values);
  }

  return (
    <Card className="w-full max-w-md">
      <CardHeader>
        <CardTitle>Create an account</CardTitle>
        <CardDescription>Create your bookstore account.</CardDescription>
      </CardHeader>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <CardContent className="space-y-4">
            <FormField control={form.control} name="email" render={({ field }) => (<FormItem><FormLabel>Email</FormLabel><FormControl><Input autoComplete="email" type="email" placeholder="you@example.com" {...field} /></FormControl><FormMessage /></FormItem>)} />
            <FormField control={form.control} name="password" render={({ field }) => (<FormItem><FormLabel>Password</FormLabel><FormControl><Input autoComplete="new-password" type="password" {...field} /></FormControl><FormMessage /></FormItem>)} />
            <FormField control={form.control} name="confirmPassword" render={({ field }) => (<FormItem><FormLabel>Confirm password</FormLabel><FormControl><Input autoComplete="new-password" type="password" {...field} /></FormControl><FormMessage /></FormItem>)} />
          </CardContent>
          <CardFooter><Button className="w-full" type="submit" disabled={registerMutation.isPending}>{registerMutation.isPending ? "Creating..." : "Register"}</Button></CardFooter>
        </form>
      </Form>
    </Card>
  );
}
