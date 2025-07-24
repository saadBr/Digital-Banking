import { Component } from '@angular/core';
import { UserService } from '../services/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { User } from '../model/user.model';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-manage-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './manage-user.html',
  styleUrl: './manage-user.css',
})
export class ManageUser {
  users: User[] = [];
  editMode: { [id: string]: boolean } = {};
  newPassword: string = '';
  selectedUser: User | null = null;
  resetPasswordValue: string = '';
  currentUsername = '';
  currentUserRoles: string[] = [];

  constructor(
    private userService: UserService,
    private toast: ToastService,
  ) {
    this.initCurrentUser();
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => (this.users = data),
      error: (err) =>
        this.toast.showError(err?.error || 'Failed to load users'),
    });
  }

  deleteUser(id: string) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.toast.showInfo('User deleted successfully');
        this.users = this.users.filter((user) => user.id !== id);
      },
      error: (err) => {
        console.error(err);
        this.toast.showError(err?.error || 'Failed to delete user');
      },
    });
  }

  enableEdit(userId: string) {
    this.editMode[userId] = true;
  }

  saveUser(user: User) {
    const updatePayload = {
      email: user.email,
      roles: user.roles,
      enabled: user.enabled,
    };
    this.userService.updateUser(user.id, updatePayload).subscribe({
      next: () => {
        this.editMode[user.id] = false;
        this.toast.showSuccess('User updated successfully');
      },
      error: (err) => {
        console.error(err);
        this.toast.showError(err?.error || 'Failed to update user');
      },
    });
  }

  toggleRole(user: User, role: string) {
    if (user.roles.includes(role)) {
      user.roles = user.roles.filter((r) => r !== role);
    } else {
      user.roles.push(role);
    }
  }

  openResetPasswordModal(user: User) {
    this.selectedUser = user;
    this.resetPasswordValue = '';
  }

  submitPasswordReset() {
    if (!this.selectedUser || !this.resetPasswordValue) {
      this.toast.showError('Please provide a new password');
      return;
    }

    this.userService
      .resetPassword(this.selectedUser.username, this.resetPasswordValue)
      .subscribe({
        next: () => {
          this.toast.showSuccess('Password reset successfully');
          this.selectedUser = null;
          this.resetPasswordValue = '';
        },
        error: (err) => {
          console.error(err);
          this.toast.showError(err?.error || 'Failed to reset password');
        },
      });
  }
  userToDelete: User | null = null;

  confirmDelete() {
    if (!this.userToDelete) return;

    const deletingSelf = this.userToDelete.username === this.currentUsername;

    this.userService.deleteUser(this.userToDelete.id).subscribe({
      next: () => {
        this.toast.showInfo('User deleted successfully');
        this.users = this.users.filter((u) => u.id !== this.userToDelete?.id);
        this.userToDelete = null;

        if (deletingSelf) {
          localStorage.clear();
          window.location.href = '/login';
        }
      },
      error: (err) => {
        console.error(err);
        this.toast.showError(err?.error || 'Failed to delete user');
        this.userToDelete = null;
      },
    });
  }

  cancelDelete() {
    this.userToDelete = null;
  }

  canDelete(targetUser: User): boolean {
    const isSelf = this.currentUsername === targetUser.username;
    const isAdmin = targetUser.roles.includes('ADMIN');
    const isCurrentSaad = this.currentUsername === 'Saad';

    if (isCurrentSaad) return true;
    if (isSelf) return true;
    return !isAdmin;
  }

  canResetPassword(targetUser: User): boolean {
    const isCurrentSaad = this.currentUsername === 'Saad';
    const isSelf = this.currentUsername === targetUser.username;

    if (isCurrentSaad) return true;

    const isAdmin = targetUser.roles.includes('ADMIN');
    return isSelf || !isAdmin;
  }

  initCurrentUser() {
    const token = localStorage.getItem('jwt-token');
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.currentUsername = payload?.sub || '';
      this.currentUserRoles = payload?.scope || [];
    }
  }
}
