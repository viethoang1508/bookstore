"use client";

import { useEffect, useState } from "react";

import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

type Category = { id: string; name: string; slug: string };

type CategoryReq = { name: string; slug: string };

async function fetchCategories() {
  const response = await apiRequest<BaseResponse<Category[]>>({ path: "/public/catalog/categories", skipAuth: true });
  return response.data;
}

export default function AdminCategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const load = async () => setCategories(await fetchCategories());

  useEffect(() => { void load(); }, []);

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Categories</h2>
      {categories.map((category) => (
        <article key={category.id} className="flex items-center justify-between rounded border p-3 text-sm">
          <div>{category.name} (/{category.slug})</div>
          <button
            type="button"
            className="text-red-600 underline"
            onClick={async () => {
              await apiRequest<BaseResponse<void>>({ path: `/admin/categories/${category.id}`, method: "DELETE" });
              await load();
            }}
          >
            Xóa
          </button>
        </article>
      ))}
    </section>
  );
}