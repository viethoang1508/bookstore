import Link from "next/link";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

export default function HomePage() {
  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-8">
      <div className="space-y-3 text-center sm:text-left">
        <h1 className="text-3xl font-bold tracking-tight sm:text-4xl">Welcome to the bookstore</h1>
        <p className="text-muted-foreground">
          Public catalog, customer checkout, and an admin console — all behind your API gateway.
        </p>
        <div className="flex flex-wrap justify-center gap-3 sm:justify-start">
          <Button asChild>
            <Link href="/books">Browse books</Link>
          </Button>
          <Button variant="outline" asChild>
            <Link href="/login">Sign in</Link>
          </Button>
        </div>
      </div>
      <div className="grid gap-4 sm:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Customer portal</CardTitle>
            <CardDescription>Cart, checkout, orders, and profile under /customer.</CardDescription>
          </CardHeader>
          <CardContent>
            <Button variant="secondary" asChild className="w-full sm:w-auto">
              <Link href="/customer/cart">Go to cart</Link>
            </Button>
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>Admin portal</CardTitle>
            <CardDescription>Dashboard, CRUD, and operations under /admin.</CardDescription>
          </CardHeader>
          <CardContent>
            <Button variant="secondary" asChild className="w-full sm:w-auto">
              <Link href="/admin">Open dashboard</Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
