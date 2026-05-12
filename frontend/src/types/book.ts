/** Placeholder DTOs — map from book_service when integrating. */
export type BookSummary = {
  id: string;
  title: string;
  author?: string;
  price?: number;
};

export type BookDetail = BookSummary & {
  description?: string;
  isbn?: string;
};
