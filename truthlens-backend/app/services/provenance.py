"""
Content provenance checking service.

Attempts to read C2PA (Content Provenance & Authenticity) metadata from
uploaded files. Falls back gracefully if the c2pa-rs SDK is not installed.
"""
import logging
from typing import Optional

logger = logging.getLogger("deepfake_detector.provenance")


def check_provenance(file_path: str, file_type: str) -> dict:
    """
    Check for C2PA content credentials in a file.

    Returns a dict with:
        has_credentials: bool
        manifest_summary: str
        details: dict
    """
    # Try importing the c2pa Python bindings (from c2pa-rs)
    # If not available, we return a graceful "not available" result.
    try:
        import c2pa  # type: ignore
    except ImportError:
        logger.info("c2pa package not installed — provenance check skipped")
        return {
            "has_credentials": False,
            "manifest_summary": "C2PA SDK not available",
            "details": {"note": "Install the c2pa package to enable content provenance verification."},
        }

    try:
        # c2pa-rs Python API: Reader reads manifests from a file path
        reader = c2pa.Reader(file_path)
        manifest_store = reader.get_manifest_store()

        if not manifest_store or not manifest_store.get("manifests"):
            return {
                "has_credentials": False,
                "manifest_summary": "No content credentials found",
                "details": {"note": "This file does not contain C2PA provenance metadata."},
            }

        manifests = manifest_store.get("manifests", {})
        active_manifest = manifests.get(manifest_store.get("active_manifest", ""))
        if not active_manifest:
            return {
                "has_credentials": False,
                "manifest_summary": "Manifest found but not readable",
                "details": {},
            }

        # Extract key provenance information
        claim_generator = active_manifest.get("claim_generator", "Unknown")
        assertions = active_manifest.get("assertions", [])
        signer = active_manifest.get("signature_info", {})

        summary_parts = [
            f"Generator: {claim_generator}",
            f"Assertions: {len(assertions)}",
            f"Signed by: {signer.get('name', 'Unknown')}",
        ]

        return {
            "has_credentials": True,
            "manifest_summary": " | ".join(summary_parts),
            "details": {
                "claim_generator": claim_generator,
                "assertion_count": len(assertions),
                "signer": signer.get("name", "Unknown"),
                "verified": signer.get("verified", False),
            },
        }

    except Exception as exc:
        logger.warning("Provenance check failed: %s", exc)
        return {
            "has_credentials": False,
            "manifest_summary": "Provenance check error",
            "details": {"error": str(exc)},
        }
