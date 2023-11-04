export interface CustomerDTO {
  id?: number;
  name?: string;
  email?: string;
  age?: number;
  gender?: 'MALE' | 'FEMALE';
  username?: string;
  userRoles?: string[];
}
