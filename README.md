# ServiceLog

An Android maintenance and cost log for your own vehicle.

Built around a real case: a 1996 VW T4 with 375,000 km on the clock, maintained
by its owner, whose entire service history used to live in a notes app.

## Using the app

### About the sample data

The app ships with a sample vehicle already loaded — a 1996 VW T4, its real
service history, the maintenance intervals it runs on, and a few refuels — so
that every screen has something to show from the first launch. Without it the
app opens empty, and an empty maintenance log cannot demonstrate much.

To start from a clean app instead, comment out the call to `seed()` inside the
`onCreate` callback in `data/AppDatabase.kt`, then uninstall and reinstall.

Note that the seed only runs when the database is created. Clearing the app's
data from Android settings deletes the database, so the sample is inserted
again on the next launch.

### Getting started

On first launch the app asks for your vehicle: type, name, make, model, year,
fuel and tank capacity, plus the current odometer reading. Only make and model
are required; everything else can be filled in later or left out.

**To edit the vehicle later, tap its card on the home screen.** This is not
signposted anywhere, and deliberately so — once the vehicle is created you will
almost never touch it again, so the action does not need to compete for space
with other things on the screen. It is still worth knowing it exists.

### Home

The vehicle card shows the current mileage and the date that reading was taken.
The **Update** button next to it records a new reading without creating a full
entry — useful for keeping the mileage-based due dates meaningful between
services.

Below it, **Next service** lists your maintenance rules, most urgent first. Each
one shows when it is due and when it was last done, so a single line answers
both questions. A coloured dot carries the status:

| Dot | Meaning |
|---|---|
| Green | In good time |
| Amber | Due soon — within two months or 1,000 km |
| Red | Overdue |
| Grey | No data yet — nothing recorded, so no estimate is shown |

Four are shown by default; tap **+N more** to expand the list.

### History

Every maintenance entry, grouped by month, with that month's total cost in the
header. Tap an entry to edit or delete it.

New entries are created with the **+** button. The form asks what was done in
free text, because real jobs rarely fit a fixed list, and then:

- **Date or mileage — either one is enough.** You do not always know both.
- **Approximate date.** Tick it when you only remember "autumn 2023". The entry
  is then shown with a tilde, so you can tell a remembered date from a recorded
  one.
- Cost, workshop and notes are all optional.

If you enter a mileage lower than the current reading, the app warns you but
still lets you save. It might be a typo, or it might be a legitimate entry from
years ago.

### Intervals

Your maintenance rules: "oil change every 12 months or 10,000 km". Months and
kilometres are both optional, as long as one is set, and whichever falls first
is the one that counts.

The **Last done** fields are only a starting point. As soon as a matching entry
exists in your history, the app uses that instead, so the rule stays in step
with what you actually did.

### Costs

Monthly totals split into fuel and maintenance, your fuel consumption, and the
list of refuels.

Logging a refuel takes four fields: date, odometer reading, litres and price.
The **Full tank** checkbox it an important bit — see below.

## How the numbers work

These are the decisions that shape the app, and the reasoning behind them.

### Mileage-based due dates are a status, not an alarm

A date-based due point can be worked out in advance: twelve months from the last
oil change is a date on the calendar. A mileage-based one cannot, because the
app has no way of knowing your odometer reading between the moments you enter
it.

So mileage due points are shown as current status and recalculated whenever a
new reading arrives — from a refuel, from an entry, or from the Update button.
This is also why the reminder notifications only cover date-based rules: it is
the only thing that can honestly be announced in advance.

### Current mileage is derived, never stored

The figure on the home screen is not a field you maintain. It is computed as the
highest reading across all maintenance entries, refuels and manual readings.

That means it cannot drift out of sync. Delete the entry that set it and the
number falls back to the next highest reading on its own.

### Fuel consumption: exact where possible, honest where not

**Between two full tanks the figure is exact.** You left with a full tank and
arrived with a full tank, so the litres you just put in are precisely what you
burned over that distance. A full tank is the only moment the fuel level is
known, which is why it is the only valid boundary.

**Everywhere else it is an average that converges.** The litres from the first
refuel are excluded — they filled a tank that was consumed before the first
measured kilometre, so counting them would inflate the result.

The unknown part is the difference in tank level between the first and last
refuel, and that difference can never exceed the tank's capacity. The distance,
meanwhile, keeps growing. So the error shrinks as you drive: with an 80-litre
tank it is roughly ±2.5 L/100km at 2,000 km, and under ±1 by 5,000 km.

Below a minimum distance the app shows nothing at all rather than a figure it
cannot stand behind.

### "No data yet" instead of a guess

When an interval has no matching entry in your history, the app says so. It's 
shown so you have an eye on it.

### Entries link to intervals by id, with category as a fallback

An entry can be tied to a specific interval. Where that link exists it takes
precedence; where it does not, the app matches by category.

Category matching alone is imprecise when several rules share one — "engine"
covers oil, timing belt, filters and water pump — so the explicit link is what
keeps an oil change from resetting the timing belt's clock.

### Money is stored as integer cents

Floating-point arithmetic accumulates rounding errors across sums, which would
show up as phantom cents in the monthly totals.

## Tech

Kotlin, Jetpack Compose, Material 3, Room, Hilt. MVVM with one ViewModel per
screen and state exposed as `StateFlow`. No backend; all data stays on the
device.

Business logic lives in `domain/` with no Android dependencies, which is what
makes the due-date and consumption calculations unit-testable without an
emulator.

```
./gradlew test
```

## Where it goes next

**Backup and a web version.** Losing your phone should not mean losing your
service history. That implies a hosted database and users.

**Several vehicles.** The data model already supports it; every entry, interval
and refuel hangs off a vehicle id. What is missing is the interface: a picker, a
notion of the active vehicle, and filtering on every screen.

**Normalised makes and models.** Today they are free text, which is fine for one
owner and a mess for many: "VW T4", "Volkswagen T4" and "Volkswagen Transporter
IV" are three rows and one van. A searchable list with a fallback for vehicles
that are not in it — classics, imports, anything unusual — would fix that without
locking anyone out.

**Suggested intervals per model.** With normalised models, a new user could
start from a sensible set of rules instead of an empty screen. Not manufacturer
schedules, which are neither freely available nor applicable to an old vehicle,
but defaults that can be edited, and eventually intervals observed from what
owners of the same model actually record.

**Approximate mileage readings.** A date can be marked as approximate; a reading
cannot.

**Smaller things.** CSV export, a photo of the invoice on each entry, a cost
chart over several months rather than the current month alone, and the reminder
that asks for an odometer reading every few weeks — the app's calculations
depend on those readings, and they are the easiest thing to forget.

## Known limitations

- Interface is English only; numbers and currency follow European conventions
  regardless of device locale.
- Reminders cover date-based intervals only, for the reason given above.
- Error handling is structurally in place but never exercised, as a local
  database has no realistic failure modes short of corruption.
- The maintenance intervals seeded for the sample vehicle come from community
  sources — an owners' wiki cross-checked against enthusiast forums — not from
  manufacturer documentation. For a thirty-year-old vehicle that is arguably the
  better source, but it is community knowledge, and every interval is editable.