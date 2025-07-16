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
  styleUrl: './manage-user.css'
})
export class ManageUser {
  users: User[] = [];
  editMode: { [id: number]: boolean } = {};
  newPassword: string = '';
  selectedUser: User | null = null;
  resetPasswordValue: string = '';

  constructor(
    private userService: UserService,
    private toast: ToastService
  ) {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: data => this.users = data,
      error: err => this.toast.showError(err?.error || "Failed to load users")
    });
  }

  deleteUser(id: number) {
    if (!confirm('Are you sure you want to delete this user?')) return;
    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.toast.showInfo("User deleted successfully");
        this.users = this.users.filter(user => user.id !== id);
      },
      error: err => {
        console.error(err);
        this.toast.showError(err?.error || "Failed to delete user");
      }
    });
  }

  enableEdit(userId: number) {
    this.editMode[userId] = true;
  }

  saveUser(user: User) {
    const updatePayload = {
      email: user.email,
      roles: user.roles,
      enabled: user.enabled
    };
    this.userService.updateUser(user.id, updatePayload).subscribe({
      next: () => {
        this.editMode[user.id] = false;
        this.toast.showSuccess("User updated successfully");
      },
      error: err => {
        console.error(err);
        this.toast.showError(err?.error || "Failed to update user");
      }
    });
  }

  toggleRole(user: User, role: string) {
    if (user.roles.includes(role)) {
      user.roles = user.roles.filter(r => r !== role);
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
      this.toast.showError("Please provide a new password");
      return;
    }

    this.userService.resetPassword(this.selectedUser.username, this.resetPasswordValue).subscribe({
      next: () => {
        this.toast.showSuccess("Password reset successfully");
        this.selectedUser = null;
        this.resetPasswordValue = '';
      },
      error: err => {
        console.error(err);
        this.toast.showError(err?.error || "Failed to reset password");
      }
    });
  }
}
