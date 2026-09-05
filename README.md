# ⚔️ enchantment_unlimit

인챈트 레벨 제한을 **없애는** Paper 플러그인. Minecraft **1.21**용.

모루(anvil)를 통한 인챈트 결합 시 레벨 상한을 걷어내고, 관리자가 임의의 인챈트를 **레벨 제한 없이** 적용할 수 있게 해줍니다.

## ✨ 주요 기능

- **모루 인챈트 무제한** — 모루에서 인챈트 레벨이 기본 상한을 넘어도 유지/결합 가능
- **커스텀 최대 레벨** — `config.yml`의 `max-levels` 섹션으로 인챈트별 최대 레벨 지정 가능
- **게임 내 부여** — 특정 플레이어의 손에 든 아이템에 원하는 인챈트/레벨 부여
- **설정 핫리로드** — `/eu reload` 로 설정 즉시 반영

## 🛠 요구사항

- **Minecraft:** 1.21 (Paper/Spigot)
- **Java:** 21
- **빌드:** Maven

## 💾 설치 / 빌드

```bash
mvn clean package
```

생성된 `target/*.jar` 를 서버 `plugins/` 폴더에 넣고 서버를 재시작합니다.

## 🕹 명령어

| 명령어 | 설명 | 권한 |
|--------|------|------|
| `/unlimitenchant <닉네임> <인챈트> <레벨>` `(/ue)` | 대상 플레이어 손에 든 아이템에 인챈트 적용 | `enchantment_unlimit.admin` |
| `/enchantment_unlimit reload` `(/eu)` | 플러그인 설정 리로드 | `enchantment_unlimit.admin` |

**권한:** `enchantment_unlimit.admin` — 기본값 `op`

## 🔧 config.yml

`max-levels` 섹션으로 인챈트별 커스텀 최대 레벨을 설정합니다.

```yaml
max-levels:
  SHARPNESS: 100
  UNBREAKING: 10
```

## 📁 소스 구조

```
src/main/java/io/github/freecad1211/enchantment_unlimit/
├── EnchantmentUnlimit.java   # 메인 플러그인 클래스
├── EnchantCommand.java       # 명령어 실행 + 탭 자동완성
└── AnvilListener.java        # 모루(PrepareAnvilEvent) 인챈트 처리
```

> 버전: 1.1 — Paper API 1.21, Maven shade 빌드.