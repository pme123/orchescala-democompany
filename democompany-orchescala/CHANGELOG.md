# Changelog

All notable changes to this project will be documented in this file.

* Types of Changes (L3):
  * Added: new features
  * Changed: changes in existing functionality
  * Deprecated: soon-to-be-removed features
  * Removed: now removed features
  * Fixed: any bug fixes
  * Security: in case of vulnerabilities


The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Operaton BPM Engine Support**: Added support for Operaton, an open-source BPM platform compatible with Camunda 7 API
  - `CompanyEngineOperatonConfig`: Configuration trait for Operaton connection settings
  - `CompanyEngineOperatonApp`: Engine application for Operaton integration
  - `CompanyOperatonSimulation`: Simulation trait for Operaton-specific simulations
  - Updated `CompanyGSimulation` to support Operaton alongside C7 and C8
  - Environment variables: `OPERATON_BASE_URL`, `OPERATON_COCKPIT_URL`
  - Example simulation: `OrderCreditcardOperatonSimulation`
  - Comprehensive documentation in `operaton-setup.md`
  - Updated pattern documentation with Operaton examples and migration guides

### Changed
- Updated orchescala dependency to 0.4.0-SNAPSHOT with Operaton support
- Enhanced pattern documentation with multi-engine support examples
- Build configuration updated to include `orchescala-worker-op` dependency

