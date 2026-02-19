# POSiT (포짓) 

> **"생각을 현실로 만드는 메모, POSiT!"**
>
> 30초면 충분한 고민 해결과 제안,
> 고객의 목소리를 가치 있게 **채택**으로 전하는 감사

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20RDS%20%7C%20S3-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Github Actions](https://img.shields.io/badge/Github%20Actions-CI%2FCD-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

## 인프라 아키텍처 및 설계 의도 (Architecture)


AWS 클라우드 환경에서 **보안(Security)**과 **배포 효율성(Maintainability)**을 최우선으로 고려하여 설계하였습니다.
![Architecture](https://github.com/user-attachments/assets/46cb7363-0037-47da-91d0-a49120919b02)

---

## 기술적 도전 및 향후 계획 (Technical Challenges & Roadmap)

### 무중단 배포를 위한 아키텍처 기반 마련 (Foundation for Zero-Downtime)

초기 설계 단계부터 서비스의 안정성과 확장성을 고려하여 **Blue/Green 무중단 배포**를 목표로 기술 스택을 선정하였습니다. 비록 한정된 개발 기간으로 인해 파이프라인 자동화까지는 도달하지 못했으나, 이를 위한 핵심 인프라 구성을 완료하였습니다.

#### 1. Nginx 리버스 프록시 도입 (Reverse Proxy)
- **도전:** 배포 중 서비스 중단을 방지하기 위해서는 트래픽을 유연하게 제어할 수 있는 진입점이 필요했습니다.
- **해결:** Spring Boot 앞단에 **Nginx**를 배치하여 리버스 프록시로 구성했습니다. 현재는 SSL(HTTPS) 처리와 단일 인스턴스로의 라우팅을 담당하고 있으나, 추후 **`upstream` 설정을 변경하여 Blue/Green 컨테이너 간 트래픽을 즉시 전환**할 수 있는 구조를 확보했습니다.

#### 2. Flyway를 이용한 DB 마이그레이션 (Database Migration)
- **도전:** 무중단 배포 시 구버전(Blue)과 신버전(Green) 애플리케이션이 동시에 DB에 접근하는 상황이 발생할 수 있어, 스키마 관리의 정합성이 필수적이었습니다.
- **해결:** **Flyway**를 도입하여 DB 스키마 변경 이력을 코드로 관리하고, 버전 관리를 자동화했습니다. 이를 통해 배포 시 발생할 수 있는 데이터베이스 불일치 문제를 예방하고, 향후 무중단 배포 시에도 안전한 스키마 변경이 가능한 토대를 마련했습니다.

### Future Scope (향후 발전 계획)
- [ ] **Blue/Green 배포 자동화:** GitHub Actions와 Nginx Reload를 연동하여 실제 무중단 배포 파이프라인 구축
- [ ] **모니터링 시스템 구축:** Prometheus & Grafana를 도입하여 서버 리소스 및 트래픽 시각화

---

## ERD
![ERD](https://github.com/user-attachments/assets/615f2fb2-b0d7-4876-a9be-7cf056a1b3d5)

