export interface BookRequest {
 title: string;
 publisherId: number | null;
 authorIds: number[];
}