"use client";

import { useEffect, useState } from "react";

import { adminPromotionsApi, type AdminPromotion, type PromotionPayload } from "@/features/admin-promotions";

const initialForm: PromotionPayload = {
  code: "",
  name: "",
  scope: "ORDER",
  type: "PERCENT",
  value: 1,
  maxDiscount: 0,
  minOrderValue: 0,
  startTime: "",
  endTime: "",
};

export default function AdminPromotionsPage() {
  const [promotions, setPromotions] = useState<AdminPromotion[]>([]);
  const [form, setForm] = useState<PromotionPayload>(initialForm);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [bookIds, setBookIds] = useState("");

  const load = async () => setPromotions(await adminPromotionsApi.list());

  useEffect(() => {
    void load();
  }, []);

  const submit = async () => {
    if (editingId) {
      await adminPromotionsApi.update(editingId, form);
    } else {
      await adminPromotionsApi.create(form);
    }
    setForm(initialForm);
    setEditingId(null);
    await load();
  };

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Promotions</h2>
      <div className="grid gap-2 rounded border p-3 md:grid-cols-3">
        <input className="rounded border px-2 py-1 text-sm" placeholder="Code" value={form.code} onChange={(e) => setForm((s) => ({ ...s, code: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="Name" value={form.name} onChange={(e) => setForm((s) => ({ ...s, name: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" placeholder="Scope" value={form.scope} onChange={(e) => setForm((s) => ({ ...s, scope: e.target.value }))} />
        <select className="rounded border px-2 py-1 text-sm" value={form.type} onChange={(e) => setForm((s) => ({ ...s, type: e.target.value as "PERCENT" | "FIXED" }))}>
          <option value="PERCENT">PERCENT</option>
          <option value="FIXED">FIXED</option>
        </select>
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Value" value={form.value} onChange={(e) => setForm((s) => ({ ...s, value: Number(e.target.value) }))} />
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Max discount" value={form.maxDiscount ?? 0} onChange={(e) => setForm((s) => ({ ...s, maxDiscount: Number(e.target.value) }))} />
        <input className="rounded border px-2 py-1 text-sm" type="number" placeholder="Min order" value={form.minOrderValue ?? 0} onChange={(e) => setForm((s) => ({ ...s, minOrderValue: Number(e.target.value) }))} />
        <input className="rounded border px-2 py-1 text-sm" type="datetime-local" value={form.startTime} onChange={(e) => setForm((s) => ({ ...s, startTime: e.target.value }))} />
        <input className="rounded border px-2 py-1 text-sm" type="datetime-local" value={form.endTime} onChange={(e) => setForm((s) => ({ ...s, endTime: e.target.value }))} />
        <button type="button" className="rounded bg-black px-3 py-2 text-sm text-white" onClick={() => void submit()}>{editingId ? "Lưu cập nhật" : "Tạo promotion"}</button>
      </div>
      {promotions.map((promotion) => (
        <article key={promotion.id} className="space-y-2 rounded border p-3 text-sm">
          <div className="flex items-center justify-between">
            <div>{promotion.code} · {promotion.status}</div>
            <div className="flex gap-3">
              <button type="button" className="underline" onClick={async () => { await adminPromotionsApi.changeStatus(promotion.id, promotion.status === "ACTIVE" ? "INACTIVE" : "ACTIVE"); await load(); }}>Bật/Tắt</button>
              <button type="button" className="underline text-blue-600" onClick={() => { setEditingId(promotion.id); setForm({ ...initialForm, code: promotion.code, name: promotion.name ?? "", scope: promotion.scope ?? "ORDER", type: (promotion.type as "PERCENT" | "FIXED") ?? "PERCENT", value: promotion.value ?? 1, maxDiscount: promotion.maxDiscount ?? 0, minOrderValue: promotion.minOrderValue ?? 0, startTime: promotion.startTime?.slice(0, 16) ?? "", endTime: promotion.endTime?.slice(0, 16) ?? "" }); }}>Sửa</button>
              <button type="button" className="text-red-600 underline" onClick={async () => { await adminPromotionsApi.delete(promotion.id); await load(); }}>Xóa</button>
            </div>
          </div>
          <div className="flex gap-2">
            <input className="flex-1 rounded border px-2 py-1" placeholder="bookId1,bookId2" value={bookIds} onChange={(e) => setBookIds(e.target.value)} />
            <button type="button" className="rounded border px-2 py-1" onClick={async () => { const ids = bookIds.split(",").map((x) => x.trim()).filter(Boolean); if (ids.length) { await adminPromotionsApi.assignBooks(promotion.id, ids); setBookIds(""); } }}>Assign books</button>
          </div>
        </article>
      ))}
    </section>
  );
}
