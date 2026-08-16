# Release and withdrawal runbook

1. Configure the repository's protected `release` environment with the owner as required reviewer.
   Protected-main CI verifies a tag is reachable from `main`, runs `check` and `cyclonedxBom`, and
   publishes an immutable SemVer package.
2. Record SBOM, compatibility result, release notes, and Git tag with the release evidence.
3. Only after publish succeeds, move the mutable `latest` release mirror. It is never a Gradle
   dependency version.
4. For an owner-authorized withdrawal, repoint or remove `latest`, record the incident, notify
   known consumers, then delete the affected GitHub Packages version. Do not reuse its version.
