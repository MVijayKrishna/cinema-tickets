# Cinema Tickets - Code Review & Documentation

**Candidate ID:** 16718899 | **Date:** April 16, 2026

---

## ✅ NAMING CONVENTIONS CHECK

### Classes
| Class Name | Convention | Status |
|---|---|---|
| `TicketServiceImpl` | PascalCase ✅ | ✅ CORRECT |
| `TicketTypeRequest` | PascalCase ✅ | ✅ CORRECT |
| `InvalidPurchaseException` | PascalCase ✅ | ✅ CORRECT |
| `Type` (enum) | PascalCase ✅ | ✅ CORRECT |

### Methods
| Method Name | Convention | Status |
|---|---|---|
| `purchaseTickets()` | camelCase ✅ | ✅ CORRECT |
| `validateAccountId()` | camelCase ✅ | ✅ CORRECT |
| `validateTicketRequests()` | camelCase ✅ | ✅ CORRECT |
| `countTicketsOfType()` | camelCase ✅ | ✅ CORRECT |
| `calculateTotalAmount()` | camelCase ✅ | ✅ CORRECT |
| `calculateTotalSeats()` | camelCase ✅ | ✅ CORRECT |

### Variables
| Variable Name | Convention | Status |
|---|---|---|
| `accountId` | camelCase ✅ | ✅ CORRECT |
| `ticketTypeRequests` | camelCase ✅ | ✅ CORRECT |
| `adultCount` | camelCase ✅ | ✅ CORRECT |
| `childCount` | camelCase ✅ | ✅ CORRECT |
| `infantCount` | camelCase ✅ | ✅ CORRECT |
| `totalTickets` | camelCase ✅ | ✅ CORRECT |
| `totalAmountToPay` | camelCase ✅ | ✅ CORRECT |
| `totalSeatsToReserve` | camelCase ✅ | ✅ CORRECT |

### Constants
| Constant Name | Convention | Status |
|---|---|---|
| `ADULT_TICKET_PRICE` | UPPER_CASE_WITH_UNDERSCORES ✅ | ✅ CORRECT |
| `CHILD_TICKET_PRICE` | UPPER_CASE_WITH_UNDERSCORES ✅ | ✅ CORRECT |
| `MAX_TICKETS_PER_TRANSACTION` | UPPER_CASE_WITH_UNDERSCORES ✅ | ✅ CORRECT |

### Packages
| Package Name | Convention | Status |
|---|---|---|
| `uk.gov.dwp.uc.pairtest` | lowercase.with.dots ✅ | ✅ CORRECT |
| `uk.gov.dwp.uc.pairtest.domain` | lowercase.with.dots ✅ | ✅ CORRECT |
| `uk.gov.dwp.uc.pairtest.exception` | lowercase.with.dots ✅ | ✅ CORRECT |

---

## 📊 METHOD LOGIC - INPUT/OUTPUT DOCUMENTATION

### 1. `purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)`

**PURPOSE:** Main entry point - validates and processes ticket purchase

**INPUT:**
```
accountId: Long
  └─ Account ID (must be > 0)
  
ticketTypeRequests: TicketTypeRequest[]
  └─ Variable number of ticket requests
  └─ Each contains: Type (ADULT/CHILD/INFANT) + quantity
```

**PROCESSING FLOW:**
```
1. validateAccountId(accountId)
   └─ Check: accountId != null && accountId > 0
   
2. validateTicketRequests(ticketTypeRequests)
   └─ Check: requests not null/empty
   └─ Check: each request not null
   └─ Check: quantities non-negative
   
3. countTicketsOfType() × 3
   └─ Count ADULT tickets
   └─ Count CHILD tickets
   └─ Count INFANT tickets
   
4. validateBusinessRules()
   └─ Check: total > 0
   └─ Check: total ≤ 25
   └─ Check: if children/infants exist, at least 1 adult exists
   └─ Check: infants ≤ adults
   
5. calculateTotalAmount()
   └─ Formula: (adults × £25) + (children × £15)
   
6. calculateTotalSeats()
   └─ Formula: adults + children (infants on laps)
   
7. Delegate to services
   └─ makePayment(accountId, totalAmount)
   └─ reserveSeat(accountId, totalSeats)
```

**OUTPUT:**
```
SUCCESS: 
  ✅ Payment service called with correct amount
  ✅ Seat service called with correct count
  ✅ Method returns void (normal completion)

FAILURE:
  ❌ InvalidPurchaseException thrown
  ❌ Services NOT called
  ❌ Exception includes descriptive error message
```

**EXAMPLES:**
```
Example 1: Valid purchase (1 Adult)
  INPUT:  accountId=1, requests=[1 Adult]
  OUTPUT: makePayment(1, 25), reserveSeat(1, 1)
  STATUS: ✅ SUCCESS

Example 2: Invalid (Child only)
  INPUT:  accountId=1, requests=[2 Child]
  OUTPUT: InvalidPurchaseException thrown
  STATUS: ❌ REJECTED

Example 3: Valid mixed (3A + 2C + 1I)
  INPUT:  accountId=42, requests=[3A, 2C, 1I]
  OUTPUT: makePayment(42, 105), reserveSeat(42, 5)
  STATUS: ✅ SUCCESS
```

---

### 2. `validateAccountId(Long accountId)`

**PURPOSE:** Validate account ID > 0

**INPUT:**
```
accountId: Long
  └─ Can be null, zero, negative, or valid (> 0)
```

**PROCESSING:**
```
if (accountId == null || accountId <= 0) {
  throw InvalidPurchaseException(
    "Invalid account ID: account ID must be greater than zero."
  )
}
```

**OUTPUT:**
```
VALID:   accountId > 0 → Silent return (no exception)
INVALID: accountId ≤ 0 → InvalidPurchaseException thrown
```

---

### 3. `validateTicketRequests(TicketTypeRequest... requests)`

**PURPOSE:** Validate ticket requests

**INPUT:**
```
requests: TicketTypeRequest[]
  └─ Array of ticket requests
  └─ Each can be: null, with negative quantity, or valid
```

**PROCESSING:**
```
1. Check if requests null or empty
   └─ If true: throw exception
   
2. Loop each request:
   └─ Check if request null → throw exception
   └─ Check if quantity < 0 → throw exception
```

**OUTPUT:**
```
VALID:   All requests non-null with non-negative quantities → Silent return
INVALID: Any request null/invalid quantity → InvalidPurchaseException thrown
```

---

### 4. `validateBusinessRules(int adults, int children, int infants, int total)`

**PURPOSE:** Enforce business rules

**INPUT:**
```
adults: int (count of adult tickets)
children: int (count of child tickets)
infants: int (count of infant tickets)
total: int (total ticket count)
```

**PROCESSING:**
```
Rule 1: At least one ticket
  if (total == 0) → throw exception

Rule 2: Maximum 25 tickets
  if (total > 25) → throw exception

Rule 3: Adult requirement
  if (adults == 0 && (children > 0 || infants > 0)) → throw exception

Rule 4: Infant lap constraint
  if (infants > adults) → throw exception
```

**OUTPUT:**
```
ALL RULES MET:     Silent return
ANY RULE BROKEN:   InvalidPurchaseException thrown with specific message
```

---

### 5. `countTicketsOfType(TicketTypeRequest[] requests, Type type)`

**PURPOSE:** Count tickets of a specific type

**INPUT:**
```
requests: TicketTypeRequest[] (array of all ticket requests)
type: Type (ADULT, CHILD, or INFANT)
```

**PROCESSING:**
```
count = 0
for each request in requests:
  if request.type == type:
    count += request.quantity
return count
```

**OUTPUT:**
```
Returns: int (total count of tickets of specified type)

Examples:
  requests=[1A, 2C, 1I], type=ADULT → 1
  requests=[1A, 2C, 1I], type=CHILD → 2
  requests=[1A, 2C, 1I], type=INFANT → 1
```

---

### 6. `calculateTotalAmount(int adults, int children)`

**PURPOSE:** Calculate total payment amount

**INPUT:**
```
adults: int (number of adult tickets)
children: int (number of child tickets)
```

**PROCESSING:**
```
Formula: (adults × ADULT_TICKET_PRICE) + (children × CHILD_TICKET_PRICE)
       = (adults × 25) + (children × 15)

Note: Infants cost £0, so excluded from formula
```

**OUTPUT:**
```
Returns: int (total amount in pounds)

Examples:
  adults=1, children=0 → (1 × 25) + (0 × 15) = 25
  adults=1, children=1 → (1 × 25) + (1 × 15) = 40
  adults=3, children=2 → (3 × 25) + (2 × 15) = 105
```

---

### 7. `calculateTotalSeats(int adults, int children)`

**PURPOSE:** Calculate total seats to reserve

**INPUT:**
```
adults: int (number of adult tickets)
children: int (number of child tickets)
```

**PROCESSING:**
```
Formula: adults + children

Note: Infants don't get seats (sit on adult's lap)
```

**OUTPUT:**
```
Returns: int (total seats needed)

Examples:
  adults=1, children=0 → 1 + 0 = 1 seat
  adults=1, children=1 → 1 + 1 = 2 seats
  adults=3, children=2 → 3 + 2 = 5 seats

Special case:
  adults=2, infants=2 → 2 + 0 = 2 seats (infants on laps)
```

---

## ✅ NAMING CONVENTIONS SUMMARY

| Aspect | Standard | Your Code | Status |
|--------|----------|-----------|--------|
| Class Names | PascalCase | ✅ Applied | ✅ PASS |
| Method Names | camelCase | ✅ Applied | ✅ PASS |
| Variable Names | camelCase | ✅ Applied | ✅ PASS |
| Constant Names | UPPER_CASE | ✅ Applied | ✅ PASS |
| Package Names | lowercase.dots | ✅ Applied | ✅ PASS |
| Enum Names | PascalCase | ✅ Applied | ✅ PASS |

---

## ✅ CODE QUALITY CHECKLIST

- ✅ Follows Java naming conventions
- ✅ Clear, descriptive method names
- ✅ Consistent variable naming
- ✅ Proper package structure
- ✅ No magic numbers (uses constants)
- ✅ Exception handling with descriptive messages
- ✅ Input validation before processing
- ✅ Immutable value objects
- ✅ Single responsibility per method
- ✅ Clean code without unnecessary complexity

---

## 🎯 FINAL VERDICT

**NAMING CONVENTIONS:** ✅ **100% COMPLIANT**

All code follows:
- ✅ Java naming conventions
- ✅ Industry best practices
- ✅ Clean code principles
- ✅ Professional standards

**READY FOR COMMIT!** ✅

---


