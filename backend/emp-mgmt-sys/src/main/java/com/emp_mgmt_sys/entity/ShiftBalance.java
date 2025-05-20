package com.emp_mgmt_sys.entity;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "year", "month"})
})
public class ShiftBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int swapCount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getSwapCount() {
        return swapCount;
    }

    public void setSwapCount(int swapCount) {
        this.swapCount = swapCount;
    }
}
