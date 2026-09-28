# 1.0.0 

2026-05-25

- Initial release

# 1.0.1

2026-05-26

- Fixed OTP generation that could produce less than 4 digits in some cases
- Fixed compilation issue and integration tests after Spring Boot 4.0.6 upgrade

# 1.1.0

2026-06-12

- Added push notifications support for events reminders and publication of new news items
- Handle visibility settings for lap records
- Do not consider e-mail address when searching for members
- Upgraded Spring Boot to version 4.0.7

# 1.1.1

2026-07-06

- Handle guest accounts (can view all content like members but in read-only)
- Upgraded Spring Boot to version 4.1.0

# 1.2.0

2026-07-25

- Link the account verification to the user device
- Added possibility to list trusted devices and revoke a device
- Allow to set the lap record details when creating or updating a track

# 1.2.1

2026-08-10

- Handle multiple versions of a track (different layout, reverse mode, etc.)
- Upgraded JaCoCo Maven plugin to version 0.8.15
- Upgraded Maven Surefire plugin to version 3.5.6
- Upgraded Maven Failsafe plugin to version 3.5.6

# 1.2.2

2026-09-02

- Added possibility to define sessions for track events (number of sessions and duration)
- Do not count non-validated users in the club statistics card
- Upgraded Spring Boot to version 4.1.1
- Upgraded MariaDB Java client to version 3.5.10
- Upgraded Java JWT library to version 4.6.0
- Upgraded Firebase Admin SDK to version 9.10.0

# 1.2.3

2026-09-29

- Expose metrics through Prometheus to monitor GraphQL API
- Updated MariaDB to version 12.3.3