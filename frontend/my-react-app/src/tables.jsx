// ============================================
// TABLE HEADERS AND ROWS FOR EACH MODULE
// ============================================

export function TableHeaders({ module }) {
  if (module === 'customers') return <><th>Name</th><th>Email</th><th>Phone</th><th>DOB</th><th>Status</th><th>Actions</th></>
  if (module === 'policies') return <><th>Number</th><th>Name</th><th>Type</th><th>Premium</th><th>Term</th><th>Customer</th><th>Actions</th></>
  return <><th>Prospect</th><th>Contact</th><th>Source</th><th>Status</th><th>Agent</th><th>Actions</th></>
}

export function TableRow({ record, module, onEdit, onDelete }) {
  if (module === 'customers') {
    return (
      <tr>
        <td>{record.firstName} {record.lastName}</td>
        <td>{record.email}</td>
        <td>{record.phoneNumber}</td>
        <td>{record.dateOfBirth}</td>
        <td>{record.accountStatus}</td>
        <td className="actions"><button onClick={onEdit}>Edit</button> <button className="danger" onClick={onDelete}>Delete</button></td>
      </tr>
    )
  }
  if (module === 'policies') {
    return (
      <tr>
        <td>{record.policyNumber}</td>
        <td>{record.policyName}</td>
        <td>{record.policyType}</td>
        <td>₹{Number(record.premiumAmount).toLocaleString()}</td>
        <td>{record.coverageTermMonths}m</td>
        <td>{record.customerName}</td>
        <td className="actions"><button onClick={onEdit}>Edit</button> <button className="danger" onClick={onDelete}>Delete</button></td>
      </tr>
    )
  }
  return (
    <tr>
      <td>{record.prospectName}</td>
      <td>{record.contactInfo}</td>
      <td>{record.referralSource}</td>
      <td>{record.leadStatus}</td>
      <td>{record.assignedAgentName}</td>
      <td className="actions"><button onClick={onEdit}>Edit</button> <button className="danger" onClick={onDelete}>Delete</button></td>
    </tr>
  )
}