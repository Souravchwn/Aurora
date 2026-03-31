import React from 'react'

interface BadgeProps {
  children: React.ReactNode
  className?: string
  variant?: 'default' | 'outline'
}

export function Badge({ children, className = '', variant = 'default' }: BadgeProps) {
  const base =
    'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium'
  const variants = {
    default: 'bg-aurora-100 text-aurora-700 dark:bg-aurora-900/30 dark:text-aurora-300',
    outline:
      'border border-slate-300 dark:border-slate-600 text-slate-600 dark:text-slate-400',
  }
  return (
    <span className={`${base} ${variants[variant]} ${className}`}>
      {children}
    </span>
  )
}
