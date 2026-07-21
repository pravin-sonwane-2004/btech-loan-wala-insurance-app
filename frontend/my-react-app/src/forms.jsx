// ============================================
// FORM FIELDS FOR EACH MODULE
// ============================================

export function FormFields({ module, form, onChange, customers }) {
  if (module === 'customers') {
    return (
      <>
        <label>First name <input name="firstName" value={form.firstName || ''} onChange={onChange} required /></label>
        <label>Last name <input name="lastName" value={form.lastName || ''} onChange={onChange} required /></label>
        <label>Email <input name="email" type="email" value={form.email || ''} onChange={onChange} required /></label>
        <label>Phone <input name="phoneNumber" value={form.phoneNumber || ''} onChange={onChange} required /></label>
        <label>DOB <input name="dateOfBirth" type="date" value={form.dateOfBirth || ''} onChange={onChange} required /></label>
        <label>Status
          <select name="accountStatus" value={form.accountStatus || 'ACTIVE'} onChange={onChange}>
            <option>ACTIVE</option><option>INACTIVE</option><option>SUSPENDED</option>
          </select>
        </label>
      </>
    )
  }
  if (module === 'policies') {
    return (
      <>
        <label>Policy number <input name="policyNumber" value={form.policyNumber || ''} onChange={onChange} required /></label>
        <label>Policy name <input name="policyName" value={form.policyName || ''} onChange={onChange} required /></label>
        <label>Type
          <select name="policyType" value={form.policyType || 'HEALTH'} onChange={onChange}>
            <option>HEALTH</option><option>LIFE</option><option>AUTO</option><option>HOME</option><option>BUSINESS</option><option>TRAVEL</option>
          </select>
        </label>
        <label>Premium <input name="premiumAmount" type="number" step="0.01" value={form.premiumAmount || ''} onChange={onChange} required /></label>
        <label>Term (months) <input name="coverageTermMonths" type="number" value={form.coverageTermMonths || ''} onChange={onChange} required /></label>
        <label>Start date <input name="effectiveStartDate" type="date" value={form.effectiveStartDate || ''} onChange={onChange} required /></label>
        <label>Customer
          <select name="customerId" value={form.customerId || ''} onChange={onChange} required>
            <option value="">-- Select a customer --</option>
            {customers.map(c => (
              <option key={c.id} value={c.id}>{c.firstName} {c.lastName} (#{c.id})</option>
            ))}
          </select>
        </label>
      </>
    )
  }
  // leads
  return (
    <>
      <label>Prospect name <input name="prospectName" value={form.prospectName || ''} onChange={onChange} required /></label>
      <label>Contact info <input name="contactInfo" value={form.contactInfo || ''} onChange={onChange} required /></label>
      <label>Referral source <input name="referralSource" value={form.referralSource || ''} onChange={onChange} required /></label>
      <label>Status
        <select name="leadStatus" value={form.leadStatus || 'NEW'} onChange={onChange}>
          <option>NEW</option><option>CONTACTED</option><option>QUALIFIED</option><option>CONVERTED</option><option>LOST</option>
        </select>
      </label>
      <label>Agent <input name="assignedAgentName" value={form.assignedAgentName || ''} onChange={onChange} required /></label>
    </>
  )
}